package com.ee.seeder.model

import java.io.File

abstract class SeedFormat(val collection: String)

case class JsonOnEachLine(override val collection: String, f: File) extends SeedFormat(collection = collection)

case class JsonFilesAreChildren(override val collection: String, f: File) extends SeedFormat(collection = collection)

case class JsonListFile(override val collection: String, f: File) extends SeedFormat(collection = collection)

