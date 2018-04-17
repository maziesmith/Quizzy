def format_json(content):
    '''
        this function is to keep all the json string data nice and uniform
         ALL LOWER CASE
    '''
    formated = {}
    new_key = ""
    new_value = ""
    for key, value in content.items():

        if type(key) == str:
            new_key = key.lower()
        else:
            new_key = key

        if type(value) == str:
            new_value = value.lower()
        else:
            new_value = value

        formated.update({new_key: new_value})

    return formated