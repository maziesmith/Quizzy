# Data Models

## User 
__this is the user of the app__
* Username 
* password 
* active

## Quizzes
__this is key to determine which question belongs to which quiz__
* Creator
* Quiz name 
* length (not needed)

## Question_pairs 
__question pairs a group of a question text and a answer__
__quizzes will consist of lists of question pairs__
__internally in the database they should look something like__
* table id 
* question text id 
* answer text id
* quiz id 

## Question_text 
__question_text is the text of the question__
* table id 
* question text 

## Answers
__answers are basically templates for all questions  this is defined by the quiz creator__
* table id 
* answer type (_boolean_ _multiple choice_ _open response_)
* answer text to be parsed (\<multiple choice A text\>, \<multiple choice B text>, <multiple choice C text\>)

## Response
__answer response__
* table id 
* answer