
def call(Closure closure = {}) {
	stage('prepare') {
		steps {

			sh '''#!/bin/bash -l
			rvm use 2.3.3
			'''

			sh '''#!/bin/bash -l
			bundle install
			'''

			closure()
		}
	}
}