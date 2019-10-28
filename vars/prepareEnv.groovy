
def call(Closure closure = {}) {
	sh '''#!/bin/bash -l
	rvm use 2.5.1
	'''

	sh '''#!/bin/bash -l
	bundle install
	'''

	closure()
}
