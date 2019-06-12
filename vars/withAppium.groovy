
def call(String script) {
	dir('appium-ui-test') {
	    deleteDir()
	    git(
	        url: 'https://github.com/i-mobility/appium-ui-test.git', 
	        branch: 'master',
	        credentialsId: '34b0992d-d6b3-4af6-9bfe-004a6987d993'
	    )
	}

	lock("appium-${env.NODE_NAME}") {

        sh """#!/bin/bash -l

		./scripts/wrappium.sh ${script}
		"""
    }


}