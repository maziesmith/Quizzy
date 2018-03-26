# from flask import Flask
# from flask_cors import CORS
from peewee import MySQLDatabase

import config



# app = Flask(__name__)
# host=config.DB_LOCATION port=config.DB_PORT
db = MySQLDatabase(
    database=config.DB_NAME,
    user=config.DB_USERNAME, password=config.DB_PASSWORD, )
# host=config.DB_LOCATION, port=config.DB_PORT,



from App.Models.Users import User
from App.Models.Quizes import Quiz

from App.Models.QuestionText import QuestionText
from App.Models.QuestionAnswer import QuestionAnswer
# from App.Models.Question import Question

db.create_tables(
	[User,
	 Quiz,
	 QuestionText,
	 QuestionAnswer], safe = True)
