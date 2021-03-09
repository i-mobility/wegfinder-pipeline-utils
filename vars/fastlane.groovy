def call(String command) {
    sh """#!/bin/bash -l
    rvm use 2.5.1
    bundle exec fastlane ${command} 
    """
}
