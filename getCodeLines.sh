#!/bin/bash

REPO_ROOT=`pwd`
USER_DATA=$REPO_ROOT/src/test/resources/user.json

grep -o '"email":.*' $USER_DATA | cut -d'"' -f4 | while read email; do
    echo "Email: $email"
done


# Using jq command

# 从文件中读取JSON字符串
#json=$(cat example.json)

# 循环遍历JSON对象的每个key，获取对应的value并输出
#for key in $(echo "${json}" | jq 'keys[]' | tr -d '"'); do
#  value=$(echo "${json}" | jq -r ".$key")
#  echo "${key} = ${value}"
#done



