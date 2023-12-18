# Elis Order Kata

Pour pratiquer le TDD sur un environnement familier aux projets Elis.

Le but du jeu est de pouvoir créer des commandes pour un jour de livraison.

### Step 1
On veut pouvoir créer une commande avec un nombre d'article prédéfini. 
Une commande est créée avec un `id`, au statut `DRAFT`, pour un jour de livraison donné.

WS: `POST /delivery-day/{id}/order`

**Entrée:**
```json
{
    "nbItems": 3
}
```

**Sortie:**

**HTTP 201**
```json
{
    "id": 1,
    "status": "DRAFT",
    "deliveryDayId": 1,
    "nbItems": 3
}
```

### Step 2
Le jour de livraison doit exister, sinon on retourne HTTP 400 `The delivery day does not exist`

### Step 3
On ne peut pas créer de commande sans article, sinon on retourne HTTP 400 `An order must have at least one item`

### Step 4
Seuls les utilisateurs externes (authority: `EXTERNAL_USER`) peuvent créer une commande, sinon on retourne HTTP 403 `Access forbidden`

### Step 5 
Il ne peut y avoir qu'une seule commande par jour de livraison, sinon on retourne HTTP 400 `An order already exists for this delivery day`

### Step 6
On ne peut créer une commande que sur un jour de livraison dans le futur, sinon on retourne HTTP 400 `An order can't be created on an expired delivery day`

### Step 7
On veut pouvoir créer aussi une commande exceptionnelle (`EXCEPTIONAL`)
- Sur le même jour de livraison que la commande régulière (`REGULAR`)
- Mêmes règles que pour la commande régulière
- Ne peut être créée que s'il existe déjà une commande régulière au statut `VALIDATED` pour le jour de livraison, sinon on retourne HTTP 400 `Exceptional order can't be created if the delivery day doesn't have a validated regular order`

**Entrée:**
```json
{
    "nbItems": 3,
    "type": "EXCEPTIONAL"
}
```

**Sortie:**
```json
{
    "id": 1,
    "status": "DRAFT",
    "deliveryDayId": 1,
    "nbItems": 3,
    "type": "EXCEPTIONAL"
}
```



