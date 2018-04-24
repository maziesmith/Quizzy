import json
from peewee import *
from App import db
from App.Models.Users import User

class Quiz(Model):

    quizname = CharField()
    # username should be a foreign key but this will work
    userid = ForeignKeyField(User,db_column='user', related_name='quiz', null =False) 
    # time and date feild would be nice for uniqueness. 

    def __str__(self):
        return json.dumps(self.__dict__["__data__"])

    def __repr__(self):
        return self.__str__()

    def to_dict(self):
        JsonData = dict()
        JsonData.update(self.__dict__["__data__"])
        # JsonData.update(self.__dict__["_obj_cache"])
        return JsonData

    class Meta:
        database = db
        # primary_key = CompositeKey('quizname', 'username')
