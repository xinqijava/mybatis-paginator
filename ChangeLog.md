## 更新日志

### 1.2.18 - 2019-06-08

#### 增加手写 count 查询支持--仿照pageHelper

增加 `countSuffix` count 查询后缀配置参数，该参数是针对 `PageInterceptor` 配置的，默认值为 `_COUNT`。

分页插件会优先通过当前查询的 msId + `countSuffix` 查找手写的分页查询。

如果存在就使用手写的 count 查询，如果不存在，仍然使用之前的方式自动创建 count 查询。

例如，如果存在下面两个查询：
```xml
<select id="selectLeftjoin" resultType="com.github.pagehelper.model.Country">
    select a.id,b.countryname,a.countrycode from country a
    left join country b on a.id = b.id
    order by a.id
</select>
<select id="selectLeftjoin_COUNT" resultType="Long">
    select count(distinct a.id) from country a
    left join country b on a.id = b.id
</select>
```
上面的 `countSuffix` 使用的默认值 `_COUNT`，分页插件会自动获取到 `selectLeftjoin_COUNT` 查询，这个查询需要自己保证结果数正确。

返回值的类型必须是`resultType="Long"`，入参使用的和 `selectLeftjoin` 查询相同的参数，所以在 SQL 中要按照 `selectLeftjoin` 的入参来使用。

因为 `selectLeftjoin_COUNT` 方法是自动调用的，所以不需要在接口提供相应的方法。