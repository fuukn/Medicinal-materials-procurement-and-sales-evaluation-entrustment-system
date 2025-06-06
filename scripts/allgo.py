# run_scripts.py
import subprocess
import time

def run_script(script_name):
    """运行指定的Python脚本并等待其完成"""
    try:
        print(f"启动 {script_name}...")
        # 使用subprocess.run等待脚本执行完成
        result = subprocess.run(['python', script_name], check=True)
        print(f"{script_name} 执行完成")
    except subprocess.CalledProcessError as e:
        print(f"运行 {script_name} 时出错: {e}")
    except FileNotFoundError:
        print(f"未找到 {script_name} 文件")
    except Exception as e:
        print(f"发生未知错误: {e}")

def main():
    # 要运行的脚本列表
    scripts = ['scripts\\datePrice.py', 'scripts\\wtest.py', 'scripts\\allWP.py']
    
    # 按顺序运行每个脚本
    for script in scripts:
        run_script(script)
        # 在脚本之间添加短暂延迟（可选）
        time.sleep(1)

if __name__ == "__main__":
    main()