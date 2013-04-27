MongoDbSeederPlugin.newSettings

testUri := "light"

testPaths := "seed/test,seed/common"

devPaths := "seed/common,seed/dev"

//always seed the db before testing: 
(test in Test) <<= (test in Test) dependsOn(seedTestTask) 



