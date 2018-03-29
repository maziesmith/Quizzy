from App import App

import config

App.run(host=config.HOST_NAME, port=config.HOST_PORT, debug=config.DEBUG)