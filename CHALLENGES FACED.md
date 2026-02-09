**more dependency of one module to another**
Post module is highly dependent on category module and image upload module,
. Category module and Image Upload module is always communicating with blog post module.
So, a lot of coupling is here, and they belong to same bounded context.

To solve this, I thought of making them one module
**Blog Module**: it consists of posts, image upload, category.

**2. Managing relationship between blog and user(author)**
user has one to many relationship with blog post, blog post has many to one relationship with user.
now my user domain model is refering to blog post domain model, which is not possible, 
to solve this problem, I will be using some design pattern.
my goal is to make system less coupled.