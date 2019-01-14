#!/usr/bin/env groovy

package at.mobility

class Environment {
	String id

	static development = Environment(id: 'development')
	static staging = Environment(id: 'staging')
	static production = Environment(id: 'production')

	static all = [
		Environment.development,
		Environment.staging,
		Environment.production
	]
}