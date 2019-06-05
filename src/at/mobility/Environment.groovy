#!/usr/bin/env groovy

package at.mobility

class Environment {
	static development = 'development'
	static staging = 'staging'
	static production = 'production'

	static all = [
		Environment.development,
		Environment.staging,
		Environment.production
	]
}