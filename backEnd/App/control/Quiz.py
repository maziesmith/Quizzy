import json

from App.control.utils import * 

from peewee import DoesNotExist, IntegrityError
from flask import Blueprint, request, Response

from App.Models.Quizes import Quiz

quiz = Blueprint('quiz', __name__)


@quiz.route('/quiz', methods=['POST'])
def create_quiz():
	content = format_json(request.get_json())

	new_quiz = Quiz()
	new_quiz.quizname = content['quizname']
	new_quiz.username = content['username']


	try:
        new_quiz.save()
    except IntegrityError:
        return Response(status=409)

    return json.dumps(new_quiz.to_dict())

@quiz.route('/quiz', methods=['GET'])
def get_all_quizes():
    return json.dumps([quiz.to_dict() for quiz in Quiz.select()])

@quiz.route("/quiz/<id_or_name>")