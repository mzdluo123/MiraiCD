# MiraiCD

简单的Mirai-Console插件持续部署辅助工具

# 安装

在`config\win.rainchan.mirai.miraicd\projects`下创建`仓库名称.yml`文件

填入下面内容，切记仓库链接需要使用`""`包裹

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

如果使用ssh仓库链接，首先需要配置deploy key，然后信任服务器密钥

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

如果你是windows，请使用下面的脚本，同时插件也会自动切换到Windows模式

```bat
@echo off
mkdir deploy_tmp
:loop
xcopy /S /Y deploy_tmp plugins
del /F /S /Q deploy_tmp
cmd /c .\mcl.cmd
timeout 1
goto loop
```

# 常见问题
## 1. 使用Nginx进行反代后，API出现502错误
这时候应当检查你的Github Webhook的Content type是否设置正确

没有反代的错误这个我倒是不清楚(~~丢给雨酱~~)

## 2. 更新时出现Git错误
like this ```Use '--' to separate paths from revisions, like this:```
这时应当检查一下你的repo_url是否配置正确，是否缺少`""`包裹