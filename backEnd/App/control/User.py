import json

from peewee import DoesNotExist, IntegrityError
from flask import Blueprint, request, Response

from App.Models.Users import User

user = Blueprint('user', __name__)


