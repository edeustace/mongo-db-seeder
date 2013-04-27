# Mongo db seeder
A library for seeding your mongo db with json files laid out in a certain way. Once installed you just call `seed-dev` to seed the dev db.

# Using in your sbt project
Have a look at the example project, but basically you do the following:

Add the dependency in project/plugins.sbt

    addSbtPlugin("com.ee" % "mongo-db-seeder-sbt" % "0.2-SNAPSHOT")
    resolvers += "ed release repo" at "http://edeustace.com/repository/snapshots"

Configure it in build.sbt

    //Import the MongoDbSeeder settings
    MongoDbSeederPlugin.newSettings
    
    //Your options here....

## build.sbt Options

The default uri is your local mongo db + the sbt project name + the mode (dev,test,prod)
EG: the example project db for dev is:
    
    mongodb://localhost:27017/mongo-db-seeder-example-dev
    
To override this add either `testUri`, `devUri` or `prodUri` eg:

    testUri := "mongodb://localhost:27017/light"

The default paths are `seed/${mode}` eg: `seed/dev`. To override this add a comma delimited list:

    testPaths := "seed/test,seed/common"

    devPaths := "seed/common,seed/dev"

#### Tip: always seed the db before testing:

    (test in Test) <<= (test in Test) dependsOn(seedTestTask)

## Seed data format
The seed data files can be laid out using any of the following conventions:

#### Single file where each line is a document in a collection and the file name matches a collection name.

eg: content.json  => will insert each line of the file into the 'content' collection

#### Single file whose name is list.json and whose contents is a json array

eg: content/list.json  => will insert each item in the array into the 'content' collection

#### Folder that contains x number of json files (and doesn't contain 'list.json' + isn't a single file)

eg: content/*.json => will insert each json file into the 'content' collection.



# Developing

## Requirements

- sbt 12
- scala 2.9.2



## Running

    sbt

## Modules
The project is split into 2 modules:

### lib
This contains the seed logic

### sbt
This hooks up that logic to a set of sbt tasks


## Example
Is under the folder 'example', just cd into it and run sbt
