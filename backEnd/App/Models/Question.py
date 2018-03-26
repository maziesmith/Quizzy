import json
from peewee import *
from App import db

from App.Models.Quizes import Quiz


class Question(Model):

	Quizid = ForeignKeyField(Quiz, null = False)
	# username should be a foreign key but this will work

	# time and date feild would be nice for uniqueness. 

	def __str__(self):
	    return json.dumps(self.__dict__["_data"])

	def __repr__(self):
	    return self.__str__()

	def to_dict(self):
	    JsonData = dict()
	    JsonData.update(self.__dict__["_data"])
	    JsonData.update(self.__dict__["_obj_cache"])
	    return JsonData

	class Meta:
	    database = db
	    # primary_key = CompositeKey('quizname', 'username')
