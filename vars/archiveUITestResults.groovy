def call() {

	publishHTML([
		allowMissing: false,
		alwaysLinkToLastBuild: false,
		keepAll: false,
		reportDir: 'appium-ui-test/reports/testResults/html', 
		reportFiles: 'index.html',
		reportName: 'UI Tests',
		reportTitles: ''
	])


	junit allowEmptyResults: true, testResults: 'appium-ui-test/reports/testResults/junit'

}