import json
from peewee import *
from App import db


class User(Model):
    username = CharField(unique = True)
    password = CharField()
    logged_in= BooleanField(default = False)

    # log_in_time this could be used for hashing 
    #  = DateTimeField()

    # question_id = ForeignKeyField(Question, to_field='id', related_name='response')

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


'''
 to create or get user 
 get_or_create(username = "frank", password = "renolds")

    this function returns a tuple with the item and a boolean of it was created (true for created false for gotten)
'''



