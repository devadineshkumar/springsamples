// MongoDB script to create database and collections for testdb
// Run these commands in the MongoDB shell or a MongoDB client

use testdb;

db.createCollection("employee");
db.createCollection("address");

 //Optionally, create indexes if needed
 db.employee.createIndex({ name: 1 });
 db.address.createIndex({ city: 1 });


// write insert script for employee collection

db.employee.insertMany([
  {
    name: "John Doe",
    position: "Software Engineer",
    department: "IT",
    salary: 60000,
    address: { street: "123 Main St", city: "Springfield", state: "IL", zip: "62701" }
  },
  {
    name: "Jane Smith",
    position: "Project Manager",
    department: "IT",
    salary: 75000,
    address: { street: "456 Oak St", city: "Springfield", state: "IL", zip: "62702" }
  },
  {
    name: "Emily Johnson",
    position: "Data Analyst",
    department: "Marketing",
    salary: 55000,
    address: { street: "789 Pine St", city: "Chicago", state: "IL", zip: "60601" }
  },
  {
    name: "Michael Brown",
    position: "HR Specialist",
    department: "HR",
    salary: 50000,
    address: { street: "101 Maple St", city: "Chicago", state: "IL", zip: "60602" }
  }
]);

 //write insert script for address collection

 db.address.insertMany([
   { street: "123 Main St", city: "Springfield", state: "IL", zip: "62701" },
   { street: "456 Oak St", city: "Springfield", state: "IL", zip: "62702" },
   { street: "789 Pine St", city: "Chicago", state: "IL", zip: "60601" },
    { street: "101 Maple St", city: "Chicago", state: "IL", zip: "60602" }
    ]);



