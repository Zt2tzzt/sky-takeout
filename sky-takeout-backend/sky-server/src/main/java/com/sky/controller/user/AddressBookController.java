package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Tag(name = "C端地址簿接口")
public class AddressBookController {
    private final AddressBookService addressBookService;

    @Autowired
    public AddressBookController(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    /**
     * 此方法用于：查询当前登录用户的所有地址信息
     *
     * @return Result<List < AddressBook>>
     */
    @GetMapping("/list")
    @Operation(summary = "查询当前登录用户的所有地址信息")
    public Result<List<AddressBook>> list() {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    /**
     * 此方法用于：新增地址
     *
     * @param addressBook 新增的 AddressBook 对象
     * @return Result<String>
     */
    @PostMapping
    @Operation(summary = "新增地址")
    public Result<String> save(@RequestBody AddressBook addressBook) {
        addressBookService.save(addressBook);
        return Result.success("新增成功");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 根据id修改地址
     *
     * @param addressBook 修改的 AddressBook 对象
     * @return Result<String>
     */
    @PutMapping
    @Operation(summary = "根据id修改地址")
    public Result<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.update(addressBook);
        return Result.success("修改成功");
    }

    /**
     * 此方法用于：设置默认地址
     *
     * @param addressBook 要修改的 AddressBook 对象
     * @return Result<String>
     */
    @PutMapping("/default")
    @Operation(summary = "设置默认地址")
    public Result<String> setDefault(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook);
        return Result.success("设置成功");
    }

    /**
     * 此方法用于：根据 id 删除地址
     *
     * @param id 要删除的 id
     * @return Result<String>
     */
    @DeleteMapping
    @Operation(summary = "根据id删除地址")
    public Result<String> deleteById(Long id) {
        addressBookService.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 此方法用于：查询默认地址
     */
    @GetMapping("default")
    @Operation(summary = "查询默认地址")
    public Result<AddressBook> getDefault() {
        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = new AddressBook();
        addressBook.setIsDefault(1);
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);

        return list != null && list.size() == 1
                ? Result.success(list.get(0))
                : Result.error("没有默认地址", null);
    }

}
