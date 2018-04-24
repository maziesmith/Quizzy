import json

from App.control.utils import * 

from peewee import DoesNotExist, IntegrityError
from flask import Blueprint, request, Response

from App.Models.QuestionText import QuestionText
from App.Models.Quizes import Quiz

questiontext = Blueprint('questiontext', __name__)

@questiontext.route('/quiz/questiontext', methods=['POST'])
def create_a_questiontext():
	content = format_json(request.get_json())

	if "quizid" in content.keys():
		try:
			quiz = Quiz.get(Quiz.id == content["quizid"])
			print(quiz)
			questiontext = QuestionText()
			questiontext.quizid = quiz 
			questiontext.text = content["questiontext"]
			questiontext.save()
		except:
			return Response(status = 409)

		return json.dumps(questiontext.to_dict())

	else:
		return Response(status = 405)


@questiontext.route('/quiz/questiontext', methods=['GET'])
def get_all_questiontexts():
	return json.dumps([QT.to_dict() for QT in QuestionText.select()])

@questiontext.route('/quiz/questiontext', methods=['PUT'])
def update_questiontext():
	content = format_json(request.get_json())

	if 'id' in content.keys():
		try:
			qtext = QuestionText.get(QuestionText.id == content['id'])
			qtext.text = content['newtext']

		except:
			return Response(status = 409)

		return json.dumps(qtext.to_dict())

	else:
		return Response(status = 405)

