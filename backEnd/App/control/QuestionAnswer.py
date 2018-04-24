import json

from App.control.utils import * 

from peewee import DoesNotExist, IntegrityError
from flask import Blueprint, request, Response

from App.Models.Users import User
from App.Models.QuestionAnswer import QuestionAnswer
from App.Models.QuestionText import QuestionText
from App.Models.Quizes import Quiz

questionanswer = Blueprint('questionanswer', __name__)


# requests for question answers should only be by id and only relational data 
@questionanswer.route('/quiz/questionanswer', methods = ['POST'])
def create_questionanswer():

	content = format_json(request.get_json())

	# this is the answe that is getting modified 
	answer = QuestionAnswer()

	# this finds the user sending the request and the information in the request 
	user = User.get( User.id == content['userid'])
	qtext = QuestionText.get(QuestionText.id == content['questiontextid'])
	quiz = Quiz.get(Quiz.id == qtext.quizid)

	if quiz.userid != user.id:
		quiz_creator = User.get( User.id == quiz.userid)

		# this check needs to be here so 
		# the user may not be able to set their answer to true. 
		# this will set their answer to right or not. 
		# providing instant feedback

		for x in QuestionAnswer.select().where(
			(QuestionAnswer.user_id == quiz_creator) & (QuestionAnswer.isRight == True)):
		
			if x.to_dict()['text'].lower() == content['answertext']:
				answer.isRight = True
				break

	else:
		# the question creator and the answer are the same thust they may change what they want. 
		if 'isright' in content.keys():
			
			if content['isright'] == True:
				answer.isRight = True
			
			elif content['isright'] == False:
				answer.isRight = False



	answer.text = content['answertext'] 
	answer.user = user 
	answer.questiontextid = qtext 
	answer.save()

	return json.dumps(answer.to_dict())

@questionanswer.route('/quiz/questionanswer/<questionid>', methods = ['GET'])
def get_answers_for_question(questionid):
	questiontext = QuestionText.get(QuestionText.id ==  questionid)
	return json.dumps([x.to_dict() for x in QuestionAnswer.select().where(QuestionAnswer.questiontextid == questiontext)])

@questionanswer.route('/quiz/questionanswer/<answerid>', methods = ['PUT'])
def update_answer(answerid):
	answer = QuestionAnswer.get(QuestionAnswer.id == answerid)

	answer.text = content['newanswertext']

	user = User.get( User.id == answer.user)
	qtext = QuestionText.get(QuestionText.id == answer.questiontextid)
	quiz_creator = User.get( User.username == quiz.username)

	# this will check the new answer
	if quiz.username != user.username:

		for x in QuestionAnswer.select().where(
			(QuestionAnswer.user_id == quiz_creator) & (QuestionAnswer.isRight == True)):
		
			if x.to_dict()['text'].lower() == content['newanswertext']:
				answer.isRight = True
				break

	else:
		# the question creator and the answer are the same thust they may change what they want. 
		if 'isright' in content.keys():
			
			if content['isright'] == True:
				answer.isRight = True
			
			elif content['isright'] == False:
				answer.isRight = False


	return json.dumps(answer.to_dict())










