#!/bin/bash

cd /home/ubuntu/pipeline

# Vendo a versão
VERSAO_JAR=$(xmllint --xpath "//*[local-name()='project']/*[local-name()='version']/text()" pom.xml)

# Definindo a permissão de execução
sudo chmod +x /home/ubuntu/pipeline/target/files-service-${VERSAO_JAR}.jar

sudo su << SERVICE
cat <<EOF > /etc/systemd/system/files-service.service
[Unit]
Description=Backend Spring Boot

[Service]
ExecStart=/usr/bin/java -jar /home/ubuntu/pipeline/target/files-service-${VERSAO_JAR}.jar
SuccessExitStatus=143
Restart=always
RestartSec=5
StandardOutput=file:/var/log/nginx/app.log
StandardError=file:/var/log/nginx/app.err.log
Type=simple
WorkingDirectory=/home/ubuntu/pipeline

[Install]
WantedBy=multi-user.target
EOF
SERVICE