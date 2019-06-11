def call() {
	stage('Review PullRequest') {
		when { changeRequest() }

		steps {
			sh '''#!/bin/bash -l
			
			bundle exec danger
			'''
		}
	}
}