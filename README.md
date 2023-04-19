# recipes_app
This is a small android application that shows recipes. User can filter them with search bar or mark them as favorite. Each recipe constists of instructions and ingredients.

## Aplication details

The application is consisted of 2 screens. 

- First screen contains the list of the recipes. User can toggle between all recipes and favorite recipes. User can also use the search bar to filter the recipes that appear on the screen.

- Second screen is the recipe screen. It is consisted of a photo of the recipe the title and the description. Cooking instructions and ingredients apart the description. User can save/remove a recipe to/from favorites. 

## Technology used

The application is developed in Android Native with Kotlin. It uses a Single Activity MVVM architecture, Retrofit for the communication with API, Room for DB and Hilt for dependency injection. Have been also created unit tests and espresso UI tests.

## Deployment instructions

The application loads the data from a mock API. You can deploy your own mock API easily with the use of [Mockoon](https://mockoon.com/download/).

Here is some sample data that can be used to populate the application with: 
```json
[  
  {    
    "id": 1,
    "url":"example1",
    "name": "Spaghetti Bolognese", 
    "ingredients": [      
      "spaghetti",      
      "ground beef",      
      "onion",      
      "garlic",      
      "tomatoes",      
      "oregano",      
      "basil",      
      "olive oil"
      ],
    "instructions": [
      "Cook spaghetti in a large pot of salted boiling water until al dente.",
      "Meanwhile, heat olive oil in a large skillet over medium heat.",
      "Add onion and garlic and cook until softened.",
      "Add ground beef and cook until browned.",
      "Stir in tomatoes and herbs and let simmer for 10 minutes.",
      "Drain spaghetti and toss with the bolognese sauce.",
      "Serve hot with grated Parmesan cheese."
    ]
  },
  {
    "id": 2,
    "url":"example2",
    "name": "Chocolate Chip Cookies",
    "ingredients": [
      "flour",
      "baking soda",
      "salt",
      "butter",
      "brown sugar",
      "white sugar",
      "eggs",
      "vanilla extract",
      "chocolate chips"
    ],
    "instructions": [
      "Preheat oven to 375°F (190°C).",
      "Mix flour, baking soda, and salt together in a bowl.",
      "In a separate bowl, cream together the butter, brown sugar, and white sugar.",
      "Beat in the eggs and vanilla extract.",
      "Stir in the flour mixture until just combined.",
      "Fold in the chocolate chips.",
      "Drop spoonfuls of dough onto a baking sheet lined with parchment paper.",
      "Bake for 8-10 minutes or until golden brown.",
      "Remove from oven and let cool on the baking sheet for 5 minutes.",
      "Transfer to a wire rack to cool completely."
    ]
  },
  {
    "id": 3,
    "url":"example3",
    "name": "Neapolitan Pizza",
    "ingredients": [
      "Pizza dough",
      "tomatoes",
      "salt",
      "mozzarella",
      "fresh basil",
      "olive oil"
    ],
    "instructions": [
      "Cover with a plastic bowl so that it’s protected from the air. Let rise for about 1 hour in a warm place or for about 3-4 hours at room temperature.",
      "Once the dough has risen it will be double in volume.",
      "Make 6 loaves, model them into spherical shapes, cover with a sheet of plastic wrap and let rise for about 45 minutes in a warm place or for a few hours at room temperature.",
      "As soon as the loaves have doubled in volume, prepare the tomato sauce and place it in a bowl.",
      "Add 1/3 of the olive oil and a pinch of salt. Knead the dough, then flatten it using your fingers. ",
      "Use a spoon or a ladle to spread a good amount of tomato sauce on the pizza. Then cover with mozzarella pieces.",
      "Garnish with a few basil leaves and bake in the oven at 480 degrees F for about 5-6 minutes. ",
      "Once ready, remove the pizza from the oven and garnish with more basil and a drizzle of oil. Serve immediately."
    ]
  },
  {
    "id": 4,
    "url": "example4",
    "ingredients": [
      "Lemon juice",
      "salt",
      "small bunch finely chopped chives",
      "mozzarella",
      "200g bag mixed salad leaves",
      "olive oil",
      "avocado"
    ],
    "instructions": [
      "Squeeze 1 tbsp lemon juice into a jam jar with a pinch of salt. ",
      "Pour in 4 tbsp olive oil, add a small bunch finely chopped chives, put on the lid, then shake well.",
      "o serve, toss with 200g bag mixed salad leaves and 2 sliced ripe avocados."
    ]
  }
]
```
Where example 1,2,3 & 4 should be replaced with actual image links.

API base url should be configured in utils/UrlConstants.kt Also modify the netconfig.xml file appropriately.
