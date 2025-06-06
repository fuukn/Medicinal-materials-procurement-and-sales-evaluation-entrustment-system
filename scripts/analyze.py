# 导入所需库
from elasticsearch import Elasticsearch
import pandas as pd
import numpy as np
from datetime import datetime
import pymysql
from statsmodels.tsa.seasonal import seasonal_decompose
import sys # Import sys to read command line arguments

# Check for command line arguments (expecting script name + 2 keywords)
if len(sys.argv) < 3:
    print("Usage: python analyze_stockpiling.py <keyword1> <keyword2>")
    sys.exit(1) # Exit if not enough arguments

keyword1 = sys.argv[1]
keyword2 = sys.argv[2]

# 1. 连接Elasticsearch获取囤货相关性评分数据
es = Elasticsearch(
    ["http://localhost:9200"],
    http_auth=("elastic", "gjz1019914")
)

# 使用动态关键词构建查询
query = {
    "query": {
        "bool": {
            "should": [
                {"match": {"content": keyword1}}, # Use keyword1 from arguments
                {"match": {"content": keyword2}}, # Use keyword2 from arguments
            ],
            "minimum_should_match": 1
        }
    },
    "size": 244,
    "_source": ["date"],
    "track_total_hits": True
}

response = es.search(index="news", body=query) # Note: DeprecationWarning for 'body' parameter still applies here. For production, consider updating Elasticsearch-py usage.

# 处理ES返回的数据
es_data = []
for hit in response['hits']['hits']:
    date_str = hit['_source']['date']
    date = datetime.strptime(date_str.split()[0], "%Y-%m-%d").date()
    es_data.append({
        'date': date,
        'score': hit['_score']
    })

es_df = pd.DataFrame(es_data)
es_df['date'] = pd.to_datetime(es_df['date'])
es_df.set_index('date', inplace=True)

# 按月聚合相关性评分
monthly_scores = es_df.resample('M').agg({'score': ['mean', 'count']})
monthly_scores.columns = ['avg_score', 'article_count']


# 2. 从MySQL获取天气和价格数据
conn = pymysql.connect(
    host='localhost',
    user='root',
    password='123456',
    database='renren_fast',
    charset='utf8mb4'
)

weather_query = """
SELECT create_date, price, average_temperature, simplified_weather
FROM price_weather_data
WHERE create_date BETWEEN '2017-01-01' AND '2025-03-31'
"""

weather_df = pd.read_sql(weather_query, conn) # Note: UserWarning for DBAPI2 connection still applies.
conn.close()

# 处理天气数据
weather_df['create_date'] = pd.to_datetime(weather_df['create_date'])
weather_df.set_index('create_date', inplace=True)
weather_df = weather_df[~weather_df.index.duplicated(keep='first')]

# 按月聚合天气和价格数据
monthly_weather = weather_df.resample('M').agg({
    'price': 'mean',
    'average_temperature': 'mean',
    'simplified_weather': lambda x: x.mode()[0] if len(x.mode()) > 0 else None
})

# 3. 合并数据
combined_df = monthly_scores.join(monthly_weather, how='outer')
combined_df = combined_df[combined_df['article_count'] > 0]

# 4. 数据分析
# 相关性分析
correlation_matrix = combined_df[['avg_score', 'price', 'average_temperature']].corr()

# 5. 深入分析 - 季节性分解 (Keep calculation, remove plot)
try:
    score_decomposition = seasonal_decompose(combined_df['avg_score'].interpolate(), model='additive', period=12)
except ValueError as e:
    pass # Keep the try-except but remove the print

# 6. 天气类型对评分的影响
weather_impact = combined_df.groupby('simplified_weather')['avg_score'].mean().sort_values(ascending=False)


# 8. 分析关键词评分与未来价格变化的关系 - 步骤 1: 初始化与数据准备
lookback_months = 2
price_change_analysis = []
combined_df.index = pd.to_datetime(combined_df.index)
try:
    combined_df = combined_df.asfreq('M')
except ValueError:
     pass


# 8. 分析关键词评分与未来价格变化的关系 - 步骤 2: 遍历与数据收集
if 'monthly_weather' in locals():
    price_change_analysis = []
    lookback_months = 2

    for index, row in combined_df.iterrows():
        current_month = index
        current_score = row['avg_score']
        current_price = row['price']

        if pd.isna(current_price):
            continue

        future_month = current_month + pd.DateOffset(months=lookback_months)

        if future_month < monthly_weather.index.min() or future_month > monthly_weather.index.max():
             continue

        is_future_month_in_monthly_weather_index = future_month in monthly_weather.index

        if not is_future_month_in_monthly_weather_index:
            continue

        try:
            future_weather_data_row = monthly_weather.loc[future_month]
            future_price = future_weather_data_row['price']

            if pd.notna(future_price):
                price_change = future_price - current_price
                price_direction = 'Increase' if price_change > 0 else ('Decrease' if price_change < 0 else 'No Change')

                price_change_analysis.append({
                    'month': current_month,
                    'avg_score': current_score,
                    'current_price': current_price,
                    'future_price': future_price,
                    'price_change': price_change,
                    'price_direction': price_direction
                })
        except KeyError:
             pass
        except Exception as e:
             pass

# 8. 分析关键词评分与未来价格变化的关系 - 步骤 3: 创建分析 DataFrame
price_change_df = pd.DataFrame(price_change_analysis)

# 8. 分析关键词评分与未来价格变化的关系 - 步骤 4: 评分分箱
if not price_change_df.empty:
    try:
        price_change_df['score_category'] = pd.qcut(
            price_change_df['avg_score'],
            q=4,
            labels=['Q1 (最低)', 'Q2', 'Q3', 'Q4 (最高)'],
            duplicates='drop'
        )
    except ValueError as e:
        if 'score_category' in price_change_df.columns:
             del price_change_df['score_category']

# 8. 分析关键词评分与未来价格变化的关系 - 步骤 5: 计算概率
if not price_change_df.empty and 'score_category' in price_change_df.columns:
    try:
        price_direction_probability = price_change_df.groupby('score_category')['price_direction'].value_counts(normalize=True).unstack(fill_value=0)
    except Exception as e:
         pass

# 8. 分析关键词评分与未来价格变化的关系 - 步骤 6: 可视化概率 (Removed)

# 7. 相关性评分总结 (Keep print)
print("\n=== 关键词相关性评分总结 ===")
if 'correlation_matrix' in locals():
    print(f"1. 评分与价格的相关性: {correlation_matrix.loc['avg_score', 'price']:.3f}")
    print(f"2. 评分与温度的相关性: {correlation_matrix.loc['avg_score', 'average_temperature']:.3f}")
if 'weather_impact' in locals() and not weather_impact.empty:
    print(f"3. 最常出现高评分天气类型: {weather_impact.idxmax()} (平均评分: {weather_impact.max():.2f})")
else:
     print("无法计算天气类型对评分的影响总结，weather_impact 数据为空或不存在。")


# 8. 分析关键词评分与未来价格变化的关系 - 步骤 7: 总结 (Keep print)
print("\n=== 关键词评分与未来价格变化分析 - 步骤 7: 总结 ===")
if 'price_direction_probability' in locals() and not price_direction_probability.empty:
    highest_score_category_label = 'Q4 (最高)'
    increase_col_label = 'Increase'

    if highest_score_category_label in price_direction_probability.index and increase_col_label in price_direction_probability.columns:
        highest_score_increase_prob = price_direction_probability.loc[highest_score_category_label, increase_col_label]
        print(f"\n最高关键词({keyword1,keyword2})评分 ({highest_score_category_label}) 出现后，未来{lookback_months}个月价格上涨的概率为: {highest_score_increase_prob:.2%}")
    else:
         print(f"\n数据中没有出现 {highest_score_category_label} 评分分箱或没有 '{increase_col_label}' 列。")

else:
     print("\n!!! 没有有效的概率数据 (price_direction_probability) 可用于总结。!!!")

print("\n=== 关键词评分与未来价格变化分析完成 ===")