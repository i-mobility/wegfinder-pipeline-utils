
def call(Closure closure = {}) {
	sh '''#!/bin/bash -l
	bundle install
	'''

	closure()
}
