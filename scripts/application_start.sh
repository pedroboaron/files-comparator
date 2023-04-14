#!/bin/bash

# Pare todos os servidores e inicie o servidor como um daemon
sudo systemctl daemon-reload
sudo systemctl stop files-service
sudo systemctl start files-service

