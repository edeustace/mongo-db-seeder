MongoDbSeederPlugin.newSettings

testUri := "mongodb://localhost:27017/some-test-db"

//testPaths := "seed/test,seed/common"

//devPaths := "seed/common,seed/dev"

//always seed the db before testing: 
(test in Test) <<= (test in Test) dependsOn(seedTestTask) 



