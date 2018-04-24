import json

from App.control.utils import * 

from peewee import DoesNotExist, IntegrityError
from flask import Blueprint, request, Response

from App.Models.Quizes import Quiz
from App.Models.QuestionText import QuestionText
from App.Models.QuestionAnswer import QuestionAnswer


quiz = Blueprint('quiz', __name__)


@quiz.route('/quiz', methods=['POST'])
def create_quiz():

	content = format_json(request.get_json())

	new_quiz = Quiz()
	new_quiz.quizname = content['quizname']
	new_quiz.userid = content['userid']

	try:
		new_quiz.save()
	
	except IntegrityError:
		return Response(status=409)

	return json.dumps(new_quiz.to_dict())


#### these are all the gets of the app

# get all the quizes 
@quiz.route('/quiz/all', methods=['GET'])
def get_all_quizes():
	return json.dumps([quiz.to_dict() for quiz in Quiz.select()])

@quiz.route('/quiz/byusername/<name>', methods=['GET'])
def get_quizes_by_user(name):
	return json.dumps([quiz.to_dict() for quiz in Quiz.select().where(Quiz.userid == userid)])

# get a single quiz by id or name or all quizes by a user 
@quiz.route("/quiz/<id>", methods =['GET'])
def get_quiz_by_id_or_name(id):

	got_quiz = None
	full_quiz = {}

	try:
		got_quiz = Quiz.get(Quiz.id == id)
		full_quiz.update(got_quiz.to_dict())

		questions = []
		
		for text in QuestionText.select().where(QuestionText.quizid == id):
			currenttext = text.to_dict()

			currenttext.update({"answers" : [answer.to_dict() for answer in QuestionAnswer.select().where(QuestionAnswer.questiontextid == text.id)]})
			questions.append(currenttext)

		full_quiz.update({"questions" : questions})

	except: 
		return Response(status = 404)

	return json.dumps(full_quiz)

@quiz.route("/quiz", methods =['POST'])
def mass_assignment(id):


@quiz.route('/quiz', methods=['PUT'])
def update_quiz_by_id_or_name():
	quiz = None
	content = format_json(request.get_json())
	if "newquizname" in content.keys() and "quizname" in content.keys():
		try:
			quiz = Quiz.get( Quiz.quizname == content["quizname"])
			quiz.quizname = content["newquizname"]
			quiz.save()

		except:
			return Response(status = 405)

	return json.dumps(quiz.to_dict())

# just needs a delete 



