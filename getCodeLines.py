#!/usr/bin/env python3

import json
import subprocess

# 读取JSON文件
with open("data.json", "r") as f:
    data = json.load(f)

# 遍历所有用户节点并输出name值
for user in data["users"]:
    name = user["name"]
    print("Name: " + name)


#======================================================

# Git命令
git_command = "git log --pretty=format:'%h - %s'"


# 执行Git命令5次
for i in range(5):
    # 执行Git命令并获取输出
    output = subprocess.check_output(git_command, shell=True)

    # 输出Git命令的结果
    print("Git log (iteration {}):\n{}\n".format(i+1, output.decode()))



# 从文件中读取JSON字符串
with open("example.json", "r") as f:
    json_str = f.read()

# 将JSON字符串解析为Python对象
data = json.loads(json_str)

# 计算name字段的数量
count = len(data["staff_id"])

print("The number of 'name' fields is:", count)
