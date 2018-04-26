import os

BASE_DIR = os.path.abspath(os.path.dirname(__file__))

HOST_NAME = "127.0.0.1"
HOST_PORT = 5000
DEBUG = True

# SECRET_KEY = "11dab799-0be9-asedf4#$*&*(@LASD4fe-9ded-651273eab8d3"

# # heruko stuff mysql
DB_NAME = "heroku_f48b68370730745"
DB_LOCATION = "us-cdbr-iron-east-05.cleardb.net"
DB_PORT = 3306
DB_USERNAME = "bba673c686ee18"
DB_PASSWORD = "34955242"

# postgres://azmlzjwhrjtyya:dcd5dcc311b76a08a5faeb8c057294481e42385cebee0c8ba6645f22f7ca40c4@ec2-50-16-196-238.compute-1.amazonaws.com:5432/dcd1u6vmc9d9q4

DB_NAME = "dcd1u6vmc9d9q4"
DB_LOCATION = "ec2-50-16-196-238.compute-1.amazonaws.com"
DB_PORT = 5432
DB_USERNAME = "azmlzjwhrjtyya"
DB_PASSWORD = "dcd5dcc311b76a08a5faeb8c057294481e42385cebee0c8ba6645f22f7ca40c4"

# aws stuff
# DB_NAME="Quizzy"
# DB_LOCATION="quizzydbusa.cwk6sqytxezl.us-east-2.rds.amazonaws.com"
# DB_PORT=3306
# DB_USERNAME="quizmaster"
# DB_PASSWORD="masterquiz"

# mysql://bba673c686ee18:34955242@us-cdbr-iron-east-05.cleardb.net/heroku_f48b68370730745?reconnect=true
# mysql://quizmaster:masterquiz@quizzydbusa.cwk6sqytxezl.us-east-2.rds.amazonaws.com/Quizzy?reconnect=true