# OrderFlow Design Tradeoffs

## Hibernate/Jpa

### `@ManyToOne` for 1:1 entity relationships

- **_`@ManyToOne` was used for one-to-one relationships instead of `@OneToOne`_**

***Relevant One-to-One Relationships***:
- `customer` <-> `user`
- `inventory_item` <-> `product`

> Since unique constraint enforces one-to-one at the database level, using @ManyToOne allows lazy fetching without
> issues of @OneToOne (prone to silently fail/revert to eager fetching, and needs strict management
> to avoid various performance/serialization bugs)

---

<br>

## Global Exception Handler

### Catching `DataIntegrityViolationException` as `HTTP 409` Conflict Error

> Ensures unguarded DB constraints produce clean responses and avoids `HTTP 500` so full exception is logged
> server-side while generic, preserving traceability without leaking stack traces.   

---

<br>

## Refresh Tokens

> OrderFlow is stateless and stateless access tokens cannot be revoked once signed. To reduce exposure, a revocable  
> refresh token is used instead. This refresh token lives server-side in Redis and only contains a random key, allowing 
> access tokens to be rotated on use (and provides basic protection from reuse of stolen tokens)  

---

<br>

## JSON Serializer (`jjwt-gson` Vs `jjwt-jackson`)

> `jjwt-gson` is the serializer used internally by `jjwt`, so using `jjwt-gson` keeps JWT JSON handling completely 
> self-contained and off the Jackson 3 stack

---

<br>

## Changes needed to deploy at scale

InventoryService:
> mapping methods use item.getProduct() (lazy association) for SKU & name. Happens in @Transactional service method,
> so it's safe,but at scale would be N+1 query on paginated list but for small number of products, isn't worth extra 
> complexity

> FIX: Use a 'JOIN FETCH' instead

---

<br>

