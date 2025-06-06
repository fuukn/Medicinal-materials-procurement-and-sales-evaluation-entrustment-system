import requests
import json
import mysql.connector
from datetime import datetime

# 数据库配置
db_config = {
    "host": "localhost",
    "user": "root",
    "password": "123456",
    "database": "renren_fast",
}

# 获取数据库中的最新日期
def get_latest_date(cursor):
    query = "SELECT MAX(create_date) FROM price_data;"
    cursor.execute(query)
    result = cursor.fetchone()
    return result[0] if result[0] else None

# =爬虫 URL
url = "https://www.zyctd.com/api/data-service/api/v1/product/getSpecPrice"
# 获取今年的年份
this_year = datetime.now().year

# 获取去年的年份
last_year = this_year - 1
payload = json.dumps({
  "areaId": 130699,
  "init": 0,
  "mId": 11306990001020200,
  "mbsId": 10202,
  "priceType": "day",
  "productId": 7,
  "tcmId": "102",
  "years": f"{last_year},{this_year}"
})
headers = {
  'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36',
  'accept': 'application/json',
  'plat_id': '1001',
  'host': 'www.zyctd.com',
  'referer': 'https://www.zyctd.com/data/price.html?id=102&type=1',
  'Content-Type': 'application/json'
}

# 发送请求并获取数据
response = requests.request("POST", url, headers=headers, data=payload)
parsed_data = json.loads(response.text)
price_list = parsed_data["data"]["ChartPriceList"]

# 连接 MySQL 数据库
try:
    connection = mysql.connector.connect(**db_config)
    cursor = connection.cursor()

    # 获取数据库中的最新日期
    latest_date = get_latest_date(cursor)
    print(f"数据库中的最新日期: {latest_date}")

    # 插入数据的 SQL 语句
    insert_query = """
    INSERT INTO price_data (create_date, price)
    VALUES (%s, %s)
    ON DUPLICATE KEY UPDATE price = VALUES(price);
    """

    # 遍历数据并插入
    for item in price_list:
        create_date_str = item["CreateDate"]
        price = item["Price"]

        # 将字符串日期转换为 datetime 对象
        create_date = datetime.strptime(create_date_str, "%Y-%m-%d").date()

        # 如果数据库中没有最新日期，或者当前数据的日期比最新日期更新
        if latest_date is None or create_date > latest_date:
            cursor.execute(insert_query, (create_date, price))
            print(f"插入数据: {create_date}, {price}")

    # 提交事务
    connection.commit()
    print("数据插入成功！")

except mysql.connector.Error as err:
    print(f"数据库错误: {err}")

finally:
    # 关闭连接
    if connection.is_connected():
        cursor.close()
        connection.close()
        print("数据库连接已关闭。")