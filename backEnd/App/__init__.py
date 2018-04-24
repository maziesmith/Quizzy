from flask import Flask
from flask_cors import CORS
from peewee import MySQLDatabase

import config


################ database and models #########################
App = Flask(__name__)
cors = CORS(App, resources={r"/*": {"origins": "*"}})

# host=config.DB_LOCATION port=config.DB_PORT
db = MySQLDatabase(
    database=config.DB_NAME,
    user=config.DB_USERNAME, password=config.DB_PASSWORD, host=config.DB_LOCATION, port=config.DB_PORT)



from App.Models.Users import User
from App.Models.Quizes import Quiz

from App.Models.QuestionText import QuestionText
from App.Models.QuestionAnswer import QuestionAnswer
# from App.Models.Question import Question


# db.drop_tables(
# 	[User,
# 	 Quiz,
# 	 QuestionText,
# 	 QuestionAnswer], safe = True)

db.create_tables(
	[User,
	 Quiz,
	 QuestionText,
	 QuestionAnswer], safe = True)




################ Routes #####################################
from App.control.User import user
App.register_blueprint(user)

from App.control.Quiz import quiz 
App.register_blueprint(quiz)

from App.control.QuestionText import questiontext
App.register_blueprint(questiontext)

from App.control.QuestionAnswer import questionanswer
App.register_blueprint(questionanswer)


