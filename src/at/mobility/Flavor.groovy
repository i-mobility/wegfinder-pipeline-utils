#!/usr/bin/env groovy

package at.mobility

class Flavor {
	String id
	String environment

	static Flavor wegfinder = Flavor(id: 'wegfinder', environment: 'default')
	static Flavor seefeld = Flavor(id: 'seefeld', environment: 'seefeld')

	static all = [
		Flavor.wegfinder,
		Flavor.seefeld
	]
}