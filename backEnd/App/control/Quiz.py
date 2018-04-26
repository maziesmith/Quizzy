import json

from App.control.utils import * 

from peewee import DoesNotExist, IntegrityError
from flask import Blueprint, request, Response

from App.Models.Quizes import Quiz
from App.Models.QuestionText import QuestionText
from App.Models.QuestionAnswer import QuestionAnswer
from App.Models.Users import User

quiz = Blueprint('quiz', __name__)


@quiz.route('/quiz', methods=['POST'])
def create_quiz():
    '''
        basic creation of quizes publish is a new field so it is checked for before 
        being changed. 
    '''

    content = format_json(request.get_json())
    user = None
    try:
        user = User.get(User.id == content['userid'])
    except:
        return Response(status = 404)

    # checking database for the existance of the quiz the user is trying to create.
    if len(list(Quiz.select().where(Quiz.userid == user, Quiz.quizname.contains(content['quizname'])))) > 0:
        return Response(status = 216)
    new_quiz = Quiz()
    new_quiz.quizname = content['quizname']
    new_quiz.userid = content['userid']

    if "published" in content.keys():
        new_quiz.published = content["published"]

    try: 
       new_quiz.save()
    
    except IntegrityError:
        return Response(status=409)

    return json.dumps(new_quiz.to_dict())


#### these are all the gets of the app

# get all the quizes 
@quiz.route('/quiz/all', methods=['GET'])
def get_all_quizes():
    try:
        query = Quiz.select()
        query.execute()
        query = list(query)
    except:
        return Response(status = 500)

    return json.dumps([quiz.to_dict() for quiz in query])

# get all of my quizes
@quiz.route("/quiz/mine/<id>", methods=["GET"])
def get_my_quizes(id):
    try:
        query = Quiz.select().where(Quiz.userid == id)
        query.execute()
        query = list(query)
    except:
        return Response(status = 500)

    return json.dumps([quiz.to_dict() for quiz in query])

# get all of the published quizes
@quiz.route("/quiz/published", methods=["GET"])
def get_published_quizes():
    try:
        query = Quiz.select().where(Quiz.published == True)
        query.execute()
        query = list(query)
    except:
        return Response(status = 500)

    return json.dumps([quiz.to_dict() for quiz in query])


@quiz.route('/quiz/byusername/<name>', methods=['GET'])
def get_quizes_by_user(name):
    try:
        query = Quiz.select().where(Quiz.userid == userid)
        query.execute()
        query = list(query)
    except:
        return Response(status = 500)
    return json.dumps([quiz.to_dict() for quiz in query])

# get a full quiz
@quiz.route("/quiz/<id>", methods =['GET'])
def get_quiz_by_id_or_name(id):

    got_quiz = None
    full_quiz = {}

    try:
        full_quiz = get_full_quizdb(id)
    except: 
        return Response(status = 404)

    return json.dumps(full_quiz)

@quiz.route("/quiz/all", methods =['POST'])
def mass_assignment():

    content = format_json(request.get_json())

    # try:
    user = User.get(User.id == content['userid'])
    # except:
    #     return Response(status = 404)

    quiz = None
    # if the id is not in the json create the quiz this should not be the case the quiz will typically be gotten
    if 'id' not in content.keys():
        # create the quiz name in the database 
        try:
            query = Quiz.select().where(Quiz.quizname == content['quizname'], Quiz.userid == user)
            query.execute()
            query = list(query)

            if len(query) == 0:
                quiz = Quiz(quizname = content['quizname'], userid = user)
                quiz.save()
            else:
                quiz = query[0]

        except:
            return Response(status = 416)
    else:
        # try:
        quiz = Quiz.get(Quiz.id == content['id'])
        # except:
        #     return Response(status = 404)


    if quiz.published == False and int(user.id) == int(quiz.userid.id):
        # if the quiz is not published and the user is not the quiz maker 

          for question in content['questions']:
                qt = None
            
                if user.id == quiz.userid.id:
                    #  if they are  they make change the questions 
                    if 'id' not in question.keys():
                        try:
                            query = QuestionText.select().where(QuestionText.text == question['text'], QuestionText.quizid == quiz)
                            query.execute()
                            query = list(query)

                            if len(query) == 0:                            
                                qt = QuestionText(text = question['text'], quizid = quiz)
                                qt.save()
                            else:
                                qt = query[0]
                        except:
                            return Response(status = 417)
                    else:
                        try:
                            qt = QuestionText.get(QuestionText.id == question['id'])
                            qt.text = question['text']
                            qt.save()
                        except:
                            return Response(status = 404)
                else:
                    # if the user.id is not quiz.id then the question exists 
                        try:
                            qt = QuestionText.get(QuestionText.id == question['id'])
                        except:
                            return Response(status = 404)                    

                # anyone may make changes to their answers 
                for answer in question['answers']:
                    qa = None 
                    query = []

                    if user.id != quiz.userid.id:
                        query = QuestionAnswer.select().where(QuestionAnswer.user == user, QuestionAnswer.questiontextid == qt)
                        query.execute()
                        query = list(query)

                    if len(query) == 0:
                        if 'id' not in answer.keys():
                            try:
                                qa = QuestionAnswer(text = answer['text'], user = user, questiontextid = qt)
                                qa.save()
                            except:
                                return Response(status = 418)
                        else:
                            try:
                                qa = QuestionAnswer.get(QuestionAnswer.id == answer['id'])
                                qa.text = answer['text']
                                qa.save()
                            except:
                                return Response(status = 404)
    else:
        return Response(status = 403)


    return  json.dumps(get_full_quizdb(quiz.id))   


@quiz.route('/quiz', methods=['PUT'])
def update_quiz_by_id_or_name():
    quiz = None
    content = format_json(request.get_json())
    if "id" in content.keys():
        try:
            quiz = Quiz.get( Quiz.id == content["id"])
            if "newquizname" in content.keys():
                quiz.quizname = content["newquizname"]
            if "published" in content.keys():
                quiz.published = content["published"]

            quiz.save()

        except:
            return Response(status = 405)

    return json.dumps(quiz.to_dict())

@quiz.route('/quiz/<id>', methods=['DELETE'])
def delete_full_quiz(id):
    if id != None:
        # create the quiz name in the database 
        try:
            quiz = Quiz.get(Quiz.id == id)
        except:
            return Response(status = 416)
    else:
        return Response(status = 404)

    # first deleting all the questions 
    for qt in QuestionText.select().where(QuestionText.quizid == quiz.id):
        try:
            QuestionAnswer.delete().where(QuestionAnswer.questiontextid == qt.id).execute()
        except:
            return Response(status = 403)

    # then deleting the questiontext 
    try:
        QuestionText.delete().where(QuestionText.quizid == quiz.id).execute()
    except:
        return Response(status = 403)

    # finally deleting the quiz
    delete_id = quiz.id
    try:
        Quiz.delete().where(Quiz.id == id).execute()
    except:
        return Response(status = 403)

    return Response(status = 200)

def get_full_quizdb(id):

    got_quiz = None
    full_quiz = {}

    got_quiz = Quiz.get(Quiz.id == id)
    full_quiz.update(got_quiz.to_dict())

    questions = []
    
    for text in QuestionText.select().where(QuestionText.quizid == id):
        currenttext = text.to_dict()

        currenttext.update({"answers" : [answer.to_dict() for answer in QuestionAnswer.select().where(QuestionAnswer.questiontextid == text.id)]})
        questions.append(currenttext)

    full_quiz.update({"questions" : questions})

    return full_quiz

