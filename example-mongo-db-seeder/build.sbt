MongoDbSeederPlugin.newSettings

//testUri := "light"

testPaths := "seed/test,seed/common"

//always seed the db before testing: 
(test in Test) <<= (test in Test) dependsOn(seedTestTask) 

