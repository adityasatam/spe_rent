const express = require('express')
const app = express()
const mongoClient = require('mongodb').MongoClient
const url = "mongodb://localhost:27017"
app.use(express.json())

mongoClient.connect(url, (err, db)=>{
    if(err) {
        console.log("Error while connecting mongo client")
    }else {
        const myDb = db.db('RentingDB')
        const collection = myDb.collection('User')

        app.post('/signup', (req, res)=>{
            const newUser = {
                user_name: req.body.user_name,
                user_mobile: req.body.user_mobile,
                user_password: req.body.user_password,
                user_fund: 0
            }
            const query = {user_mobile: newUser.user_mobile}
            collection.findOne(query, (err, result)=>{
                if(result == null){
                    collection.insertOne(newUser, (err, result)=>{
                        res.status(200).send()
                    })
                } else {
                    res.status(400).send()
                }
            })
        })

        app.post('/login', (req, res)=>{
            const query = {
                user_mobile: req.body.user_mobile,
                user_password: req.body.user_password
            }
            collection.findOne(query, (err, result)=>{
                if(result != null){
                    const objToSend = {
                        user_name: result.user_name, 
                        user_mobile: result.user_mobile,
                        user_fund: result.user_fund
                    }

                    res.status(200).send(JSON.stringify(objToSend))

                } else {
                    res.status(404) //object not found
                }
            })
        })

        app.post('/user_info', (req, res)=>{
            const query = {
                user_mobile: req.body.user_mobile
            }
            collection.findOne(query, (err, result)=>{
                if(result != null){
                    const objToSend = {
                        user_name: result.user_name, 
                        user_mobile: result.user_mobile,
                        _id: result._id,
                        user_fund: result.user_fund
                    }
                    console.log("result", result.user_name);
                    res.status(200).send(JSON.stringify(objToSend))
                } else {
                    res.status(404) //object not found
                }
            })
        })

        app.post('/add_funds', (req, res)=>{
            var user_mobile = req.body.user_mobile
            var user_fund = req.body.user_fund
            collection.findOneAndUpdate({
                user_mobile : user_mobile
            },{ $set: {
                    user_fund: user_fund 
                }
            })
            res.status(200).send();
        })
        
        // Product Relations...
        const productCollection = myDb.collection('Products')
        app.post('/add_product', (req, res)=>{
            const query = {
                product_name: req.body.product_name,
                product_description: req.body.product_description,
                product_rent_price: req.body.product_rent_price,
                owner_location: req.body.owner_location,
                user_mobile: req.body.user_mobile,
                product_image: req.body.product_image,
                isRented: req.body.isRented,
                rented_user_name: req.body.rented_user_name,
                rented_user_mobile: req.body.rented_user_mobile
            }
            // console.log("QuerryRuN")
            productCollection.insertOne(query, (err, result)=>{
                if(result!=null)
                    res.status(200).send()
                else 
                    res.status(400).send()
            })
        })

        app.post('/add_rent_product', (req, res)=>{
            var product_name = req.body.product_name
            var user_mobile = req.body.user_mobile
            // var _id = ObjectId(req.body._id)
            console.log(product_name, user_mobile);
            productCollection. updateOne({
                user_mobile : user_mobile,
                product_name: product_name
                // _id: _id
                },{ $set: {
                    isRented: req.body.isRented,
                    rented_user_name: req.body.rented_user_name,
                    rented_user_mobile: req.body.rented_user_mobile
                }
            }, (err, result) =>{
                if(result!=null){
                    console.log("return...")
                    res.status(200).send()
                }
                else {
                    console.log("reject...")
                    res.status(400).send()
                }
            })
        })
        

        app.post('/get_products', (req, res)=>{
           productCollection.find().toArray(function(err, docs){
               if(docs!=null){
                    console.log(docs);
                    // console.log(JSON.stringify(docs));
                    docs.forEach(function(doc) {
                        console.log(doc.product_name + " is a " + doc.product_description);
                    })
                    res.status(200).send(JSON.stringify(docs));
               } else {
                 res.status(404) //object not found
               }
           })
        })

        app.post('/get_products_by_id', (req, res)=>{
            const query = {
                user_mobile: req.body.user_mobile
            }
            productCollection.find(query).toArray(function(err, docs){
                if(docs!=null){
                     console.log(docs);
                     console.log(JSON.stringify(docs));
                     docs.forEach(function(doc) {
                         console.log(doc.product_name + " is a " + doc.product_description);
                     })
                     res.status(200).send(JSON.stringify(docs));
                } else {
                  res.status(404) //object not found
                }
            })
         })

         //Notification...
         const notificationCollection = myDb.collection('Notification')
         app.post('/add_notification', (req, res)=>{
            const query = {
                request_user_mobile: req.body.request_user_mobile,
                product_id: req.body.product_id,
                product_image: req.body.product_image,
                product_owner_mobile: req.body.product_owner_mobile,
                date_time: req.body.date_time,
                request_user_name: req.body.request_user_name,
                product_name: req.body.product_name,
                product_rent_price: req.body.product_rent_price,
            }
            console.log("QuerryRuN")
            const findQueryy = {
                request_user_mobile: req.body.request_user_mobile,
                product_id: req.body.product_id
            }
            notificationCollection.find(findQueryy).toArray(function(err, docs){
                console.log(docs);
                if(docs!=null && docs.length>0){
                    console.log("found...")
                    res.status(101).send();
                }
                else {
                    console.log("Not found...")
                    notificationCollection.insertOne(query, (err, result)=>{
                        if(result!=null)
                            res.status(200).send()
                        else 
                            res.status(400).send()
                    })
                }
            })
           
        })



        app.post('/get_notification', (req, res)=>{
            const query = {
                product_owner_mobile: req.body.user_mobile
            }
            notificationCollection.find(query).toArray(function(err, docs){
                if(docs!=null){
                    //  console.log(docs);
                    //  console.log(JSON.stringify(docs));
                     docs.forEach(function(doc) {
                         console.log(doc.product_name + " is a " + doc.product_owner_mobile);
                     })
                     res.status(200).send(JSON.stringify(docs));
                } else {
                    console.log("No Data")
                  res.status(404) //object not found
                }
            })
         })

         app.post('/reject_notification', (req, res)=>{
            const query = {
                product_owner_mobile: req.body.product_owner_mobile,
                product_id: req.body.product_id,
                request_user_mobile: req.body.request_user_mobile
            }
            console.log(query);
            notificationCollection.deleteOne(query, function(err, obj) {
                if (err) {
                    console.log("No Data")
                    res.status(404) //object not found
                } else {
                    console.log("1 document deleted");
                    res.status(200).send();
                }
              });
         })

    }
})

app.listen(3000, ()=>{
    console.log("Listening on port 3000...")
})
