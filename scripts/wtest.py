import requests
import json
from bs4 import BeautifulSoup
import mysql.connector 
import re
from datetime import datetime

# 请求头
headers = {
    
}

# 数据库配置
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'renren_fast'
}

# 连接数据库
conn = mysql.connector.connect(**db_config)
cursor = conn.cursor()

def get_last_date_in_weather():
    """获取weather表中最新的日期"""
    query = """
    SELECT MAX(date) 
    FROM weather
    """
    cursor.execute(query)
    result = cursor.fetchone()[0]  # 获取最大日期
    return result

def save_data(weather_data):
    """保存天气数据到数据库"""
    sql = """
    INSERT INTO weather (date, highest_temperature, lowest_temperature, weather, wind, air_quality) 
    VALUES (%s, %s, %s, %s, %s, %s)
    """
    cursor.execute(sql, (
        weather_data['date'], 
        weather_data['highest_temperature'], 
        weather_data['lowest_temperature'], 
        weather_data['weather'], 
        weather_data['wind'], 
        weather_data['air_quality']
    ))
    conn.commit()

def get_weather_data(url, params, headers, last_date):
    """获取天气数据并避免重复插入"""
    # 发送 GET 请求
    response = requests.get(url, params=params, headers=headers)
    data_json = response.json()

    # 获取 data 部分的 HTML 数据
    html_content = data_json['data']

    # 解析 HTML
    soup = BeautifulSoup(html_content, 'html.parser')

    # 提取表格中的数据
    rows = soup.select('.history-table tr')
    for row in rows[1:]:  # 第一行是表头，跳过
        columns = row.find_all('td')
        if len(columns) == 6:
            date = columns[0].get_text()
            high_temp = columns[1].get_text().strip('°')
            low_temp = columns[2].get_text().strip('°')
            weather = columns[3].get_text()
            wind = columns[4].get_text()
            aqi = columns[5].get_text().split()[0]
            clean_date = re.match(r'\d{4}-\d{2}-\d{2}', date).group()

            # 检查日期是否晚于已有最新日期
            if last_date and clean_date <= str(last_date):
                continue  # 如果日期早于或等于最新日期，跳过

            weather_data = {
                'date': clean_date,
                'highest_temperature': high_temp,
                'lowest_temperature': low_temp,
                'weather': weather,
                'wind': wind,
                'air_quality': aqi
            }
            save_data(weather_data)
            print(f"插入数据: {clean_date}")

# 接口 URL
url = 'https://tianqi.2345.com/Pc/GetHistory'

# 请求头
headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36',
    'Referer': 'https://tianqi.2345.com/',
    'Accept-Language': 'en-US,en;q=0.9',
    'Accept-Encoding': 'gzip, deflate, br',
    'Connection': 'keep-alive'
}

# 获取最新日期
last_date = get_last_date_in_weather()
print(f"最新日期: {last_date}")
# 获取当前日期（使用系统默认时区）
current_date = datetime.now()
# 获取月份（整数形式，1-12）
month = current_date.month 
print(f"当前月份: {month}")
# 循环获取数据（从2025年到2024年，月份从12到1）
for i in range(2025, 2024, -1):  
    for j in range(month, 0, -1):
        params = {
            'areaInfo[areaId]': '60930',  # 目标地区ID
            'areaInfo[areaType]': '2',    # 地区类型
            'date[year]': i,         # 年份
            'date[month]': j         # 月份
        }
        print(f"获取 {i} 年 {j} 月的数据")
        get_weather_data(url=url, params=params, headers=headers, last_date=last_date)

# 关闭数据库连接
cursor.close()
conn.close()