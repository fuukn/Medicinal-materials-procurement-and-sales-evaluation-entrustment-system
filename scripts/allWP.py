import mysql.connector
from datetime import datetime, timedelta

# 数据库连接配置
DB_CONFIG = {
    'host': "localhost",
    'user': "root",
    'password': "123456",
    'database': "renren_fast"
}

def connect_db():
    """建立数据库连接"""
    return mysql.connector.connect(**DB_CONFIG)

def get_last_date_in_price_weather():
    """获取price_weather_data表中最新的日期"""
    conn = connect_db()
    cursor = conn.cursor()
    
    query = """
    SELECT MAX(create_date) 
    FROM price_weather_data
    """
    
    cursor.execute(query)
    result = cursor.fetchone()[0]
    cursor.close()
    conn.close()
    return result

def get_last_10_days_prices(last_date=None):
    """从price_data表获取价格数据（不超过昨天）"""
    conn = connect_db()
    cursor = conn.cursor()
    yesterday = (datetime.now() - timedelta(days=1)).date()

    if last_date:
        # 获取比last_date新但不包括今天的数据
        query = """
        SELECT pid, price, create_date 
        FROM price_data 
        WHERE create_date > %s AND create_date <= %s
        ORDER BY create_date ASC
        """
        cursor.execute(query, (last_date, yesterday))
    else:
        # 首次获取时，只获取昨天及之前的数据
        query = """
        SELECT pid, price, create_date 
        FROM price_data 
        WHERE create_date <= %s
        ORDER BY create_date DESC
        """
        cursor.execute(query, (yesterday,yesterday))
    
    results = cursor.fetchall()
    cursor.close()
    conn.close()
    
    # 如果是首次获取，需要将结果按日期升序排列
    if not last_date:
        results = sorted(results, key=lambda x: x[2])
    
    return results

def get_weather_by_date(date):
    """根据日期从weather表获取天气数据"""
    conn = connect_db()
    cursor = conn.cursor()
    
    query = """
    SELECT highest_temperature, lowest_temperature, weather, wind, air_quality
    FROM weather 
    WHERE date = %s
    """
    
    cursor.execute(query, (date,))
    result = cursor.fetchone()
    
    cursor.close()
    conn.close()
    return result

def simplify_weather(weather):
    """简化天气描述"""
    if not weather:
        return "未知"
    weather = str(weather)
    if "晴" in weather:
        return "晴"
    elif "雨" in weather:
        return "雨"
    elif "雪" in weather:
        return "雪"
    elif "云" in weather or "阴" in weather:
        return "多云"
    else:
        return "其他"

def insert_price_weather_data():
    """主函数：处理数据并插入到price_weather_data表"""
    try:
        last_date = get_last_date_in_price_weather()
        price_data = get_last_10_days_prices(last_date)
        
        if not price_data:
            print("没有新的价格数据需要插入")
            return
        
        conn = connect_db()
        cursor = conn.cursor()
        inserted_count = 0
        
        for price_row in price_data:
            pid, price, create_date = price_row
            
            # 检查是否已存在该日期的记录
            check_query = """
            SELECT COUNT(*) 
            FROM price_weather_data 
            WHERE create_date = %s
            """
            cursor.execute(check_query, (create_date,))
            if cursor.fetchone()[0] > 0:
                continue
            
            # 获取天气数据（确保日期不超过昨天）
            weather_data = get_weather_by_date(create_date)
            
            # 准备插入的数据
            if weather_data:
                highest_temp, lowest_temp, weather, wind, air_quality = weather_data
                avg_temp = (highest_temp + lowest_temp) / 2 if highest_temp and lowest_temp else None
                simplified_weather = simplify_weather(weather)
            else:
                highest_temp = lowest_temp = avg_temp = None
                weather = wind = air_quality = None
                simplified_weather = "未知"
            
            # 插入数据
            insert_query = """
            INSERT INTO price_weather_data (
                create_date, price, lowest_temperature, highest_temperature,
                weather, wind, air_quality, simplified_weather, average_temperature
            ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
            """
            
            cursor.execute(insert_query, (
                create_date, price, lowest_temp, highest_temp,
                weather, wind, air_quality, simplified_weather, avg_temp
            ))
            inserted_count += 1
        
        conn.commit()
        print(f"成功插入 {inserted_count} 条新数据到price_weather_data表")
        
    except Exception as e:
        print(f"发生错误: {str(e)}")
        if 'conn' in locals():
            conn.rollback()
    finally:
        if 'cursor' in locals():
            cursor.close()
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    insert_price_weather_data()