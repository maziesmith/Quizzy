import json
from peewee import *
from App import db
from App.Models.Users import User
from App.Models.QuestionText import QuestionText


class QuestionAnswer(Model):


    text = CharField(null = False)
    # this field should not be able to be set it should be set when making the quiz 
    isRight = BooleanField(default = False, null = False)
    username = ForeignKeyField(User, null = False)
    quizid = ForeignKeyField(QuestionText, null = False)
    # time and date feild would be nice for uniqueness. 

    def __str__(self):
        return json.dumps(self.to_dict())

    def __repr__(self):
        return self.__str__()

    def to_dict(self):
        JsonData = dict()
        JsonData.update(self.__dict__["__data__"])
        # JsonData.update(self.__dict__["_obj_cache"])
        return JsonData

    class Meta:
        database = db


# to make a question answer a user must exist