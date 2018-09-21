#REST endpoint documentation

### Note: a user with the `admin` role has access to everything (good for testing)

# `/projects` - retrieve project data

_Note: Data structure depends on the `AbstractProject` class implementation._

- GET `/all` - get a list of all projects currently stored in the DB (`admin` only!)

- GET `/` - get a list of all projects the user is allowed to access

- GET `/{projectId}` - get a single project by its ID

- _TODO_: POST / - create a new project

# `/users` - retrieve and set user data
    
- GET `/{userId}/` - get a user by its ID
        
Example response:
```
{
    "id": "user",
    "firstName": "Test",
    "lastName": "User",
    "email": "test.user@example.com",
    "lob": "LOB Test",
    "accessInfo": {
        "hasAccess": true,
        "accessStart": "2018-09-21T16:22:34.005",
        "accessEnd": "2018-09-21T16:37:00"
    },
    "applications": {
        "count": 0,
        "path": "/users/daniel/applications"
    },
    "bookmarks": {
        "count": 0,
        "path": "/users/daniel/bookmarks"
    },
    "staff": {
        "count": 2,
        "path": "/users/user/staff"
    }
}
```

- GET `/{userId}/staff` - get all staff members of the user

Example Response:

```
[
    {
        "id": "user",
        "firstName": "Test",
        "lastName": "User",
        "email": "test.user@example.com",
        "lob": "LOB Test",
        "accessInfo": {
            "hasAccess": false,
            "accessStart": null,
            "accessEnd": null
        },
        "applications": {
            "count": 0,
            "path": "/users/daniel/applications"
        },
        "bookmarks": {
            "count": 0,
            "path": "/users/daniel/bookmarks"
        }
    }
]
```

- GET `/{userId}/applications` - get a list of all applications of this user

Example response: (not yet implemented)
```
{
	"id": 1337,
	"user": 
	{
		"id": "user",
		"applications": 
		{
			"count": 2,
			"path": "/users/user/applications
		},
		"bookmarks":
		{
			"count": 5,
			"path": "/users/user/bookmarks"
		}
	},
	"project": {
		<-- depends on data struture -->
	},
	"comment": "Application comment",
	"applicationDate": "2018-09-07T13:37:00"
}
```

- POST `/{userId}/applications` - apply for a project

Example request body:
```
{
	"projectId": 1337,
	"comment": "Optional comment!"
}
```

Example response:
```
{
	"id": 1,
	"user": 
	{
		"id": "user",
		"applications": 
		{
			"count": 2,
			"path": "/users/user/applications
		},
		"bookmarks":
		{
			"count": 5,
			"path": "/users/user/bookmarks"
		}
	},
	"project": {
		"id": 1337,
		.
		.
		.
	},
	"comment": "Optional comment!",
	"applicationDate": "2018-09-07T13:37:00"
}
```

- GET `/{userid}/bookmarks` - get a list of all bookmarked projects of this user

Outputs a JSON Array of all bookmarked projects, depends on project data structure

- POST `/{userId}/bookmarks` - bookmark a project

Example Request Body:
```
{
	"projectId": 1337
}
```

Example Response Body:
```
{
	"id": 1337,
	"title": "Title",
	.
	.
	.
}
```

- DELETE `/{userId}/bookmarks/{projectId}` - delete a bookmark of a project

Example Response:
```
{
    "user": {
        "id": "daniel",
        "applications": {
            "count": 0,
            "path": "/users/daniel/applications"
        },
        "bookmarks": {
            "count": 0,
            "path": "/users/daniel/bookmarks"
        }
    },
    "hasAccess": true,
    "accessStart": "2018-09-11T10:42:22.492",
    "accessEnd": "2018-09-20T19:00:00"
}
```

- POST `/{userId}/access` - give users access to projects/applications etc.

Example request body:
```
{
	"accessEnd": "2018-09-23T13:37:00"
}
```

Example response:
```
{
	"id": "user",
    "firstName": "Test",
    "lastName": "User",
    "email": "test.user@example.com",
    "lob": "LOB Test",
    "accessInfo": {
        "hasAccess": true,
        "accessStart": "2018-09-20T15:00:00",
        "accessEnd": "2018-09-23T13:37:00"
    },
    "applications": {
        "count": 0,
        "path": "/users/daniel/applications"
    },
    "bookmarks": {
        "count": 0,
        "path": "/users/daniel/bookmarks"
    },
	"staff": {
		"count": 2,
        "path": "/users/user/staff"
	}
}
```

- DELETE `/{userId}/access` - withdraw access to projects/applications etc.

```
{
    "id": "user",
    "firstName": "Test",
    "lastName": "User",
    "email": "test.user@example.com",
    "lob": "LOB Test",
    "accessInfo": {
        "hasAccess": false,
        "accessStart": null,
        "accessEnd": null
    },
    "applications": {
        "count": 0,
        "path": "/users/daniel/applications"
    },
    "bookmarks": {
        "count": 0,
        "path": "/users/daniel/bookmarks"
    }
}
```

##Note: this has no effect on REST authorization when the `user-access` profile is not activated!