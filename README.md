# MiraiCD

简单的Mirai-Console插件持续部署辅助工具

# 安装

在`config\win.rainchan.mirai.miraicd\projects`下创建`仓库名称.yml`文件

填入下面内容

```yml
repo_url: "仓库链接"
branch: master
```
这样当push新代码到指定分支即可自动部署到mirai console中

如果需要tag部署请按照下面的配置填写
```yml
repo_url: "仓库链接"
tag_regex: "v.*"
```

之后需要在GitHub对应仓库中添加webhook
* url: http://你的服务器地址:5412/webhook
* Content type: json
* Which events: just push

收到webhook之后将会自动完成插件编译并重启mirai-console

重启需要一点别的东西来辅助，使用下面的自动重启shell脚本来在console关闭后自动拉起console

```shell
while true 
do
    ./mcl
    sleep 1 
done
```
