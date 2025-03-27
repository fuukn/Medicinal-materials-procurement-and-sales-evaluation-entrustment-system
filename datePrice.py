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
  'cookie': 'Hm_lvt_ba57c22d7489f31017e84ef9304f89ec=1740888506,1740888674,1740919720,1741240263; HMACCOUNT=357CE22B17434890; .AspNetCore.Antiforgery.ioMw_bgvklE=CfDJ8OSR8LMt_fFCnvleLBelZ6h5f9Oxg40qrlm1FV3VKV7osxdOzPPkIojBA007ElDgGLTKHrj17Ebc7to3HU9rgT1pcShgLM8p4ofp4xEnAy_C9e8FxM__dv__A9qBKqlpCn8WimfRw31_JRjnp8eI6uw; FromsAuthByDbCookie_zytd_Edwin.PrvGuest=1c1uuuuuuuuuuaI7S9UV5SU16UUW185RS1c4aa19bRc69a859Vd0o5qqnc78ca99o698qac9nadmoll5moad015; ZyctdAuthToken=a4272d9c8d8d47029dc53ac565592fad; zyctd.auther=CfDJ8OSR8LMt_fFCnvleLBelZ6imPOpdH_Fh2ZQsIuph5spiijbHZweoXVfAZ51Vpv1vvwFAb_nTVXMeT88h4-CsP3gA_jW6DIz3HcxLRN25kmoDe_zVsCPOC_yOUqiCIpU6jkG4n1d-BLSptXO8dgA8uiO61AQ5UKbu8pHzeGJcdeDTdLRAmmQgp7cGOQAgh8dzwrWTPUXS8U-AfHNvQ9CguCF5MzGqNK1l3azCXg6IMQX0k7-X_NGFYV4IKfQqrBPw8CWevUaJu54dmMqb7IQkwGAuIU3ufnOzhIrwN3kILwhgowN1CN_TCPuD5pjQ-5zTTUScBn3NSxm7-ZWfvQjZJrJP1CbZX5bruCdMCVoai_w2w5MNG2WQ7QmuYDX89baASsm9ZClxd8aI974K0Tpff9isfy9wcYUZV3jzIJJr4YOZikR-wi8ZMsA6_WIVe5Q2hGk-ZEDD_JteR8P4oQMsqvEfvdTv9Z9y5S2CSw8HDyAzOEWPJmEnTDHcbyeeiVUbOPMt60-xQeK59ZW1L2MEwNd40I5gLSPcVOsL3Oo6lmjNdQaeWaD8shBJ02KocDBqvj6FenwEEidO1syaX64uLF0QiuGduziIMZSqBlMguyXgiMu62BiSLy1vVwP2i90gnBOOI6PzfTkLJH7IPgxnQX8ofKasQzeCpHE17pm-c23Xf_cXO3JD-qYbVCendULvgwPJxp8qqVl6HGvgYMKQ_MPhfPXMZYgaVCi3YStIEHpwo9syKPZ6wYiZ24dG__eFVgoEzSKIBolz2I-82WLOemdlL5tZZabD5zgFaOOXzAYdE5mVUs77riOfIPVp8wihauIu8Y8JRQCVcAXICU48QXpzq64K9nJ4JxT81Swa5NNl; Hm_lpvt_ba57c22d7489f31017e84ef9304f89ec=1741242619',
  'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36',
  'accept': 'application/json',
  'plat_id': '1001',
  'host': 'www.zyctd.com',
  'referer': 'https://www.zyctd.com/data/price.html?id=102&type=1',
  'Token': 'a4272d9c8d8d47029dc53ac565592fad',
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