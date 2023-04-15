#!/bin/bash

# Pare todos os servidores e inicie o servidor como um daemon
sudo systemctl stop files-comparator

# Removendo o systemd
sudo rm -f /etc/systemd/system/files-comparator.service

# Removendo diretorio antigo
sudo rm -rf /home/ubuntu/files-comparator
