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

## Global Exception Handler

### Catching `DataIntegrityViolationException` as `HTTP 409` Conflict Error

> Ensures unguarded DB constraints produce clean responses and avoids `HTTP 500` so full exception is logged
> server-side while generic, preserving traceability without leaking stack traces.   

---
