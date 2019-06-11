def call() {
	sh '''#!/bin/bash -l
	
	bundle exec danger
	'''
}