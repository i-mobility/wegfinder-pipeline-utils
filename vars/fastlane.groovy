def call(String command) {
    sh """#!/bin/bash -l
    bundle exec fastlane ${command} 
    """
}
