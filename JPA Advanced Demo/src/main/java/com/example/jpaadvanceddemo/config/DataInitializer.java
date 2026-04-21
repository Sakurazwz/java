package com.example.jpaadvanceddemo.config;

import com.example.jpaadvanceddemo.entity.*;
import com.example.jpaadvanceddemo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final CourseRepository CourseRepository;
    private final StudentRepository StudentRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 检查是否已有数据，避免重复初始化
        if (userRepository.count() > 0) {
            return;



        }


        System.out.println("开始初始化测试数据...");
        initDepartmentAndEmployee(); // 一对多数据（新增）
        initStudentAndCourse();

        // 创建用户1及其详情
        User user1 = new User();
        user1.setUsername("zhangsan");
        user1.setPassword("123456");
        user1.setEmail("zhangsan@example.com");
        user1.setPhone("13800138000");
        user1.setStatus(1);

        UserProfile profile1 = new UserProfile();
        profile1.setRealName("张三");
        profile1.setGender(1);
        profile1.setBirthday(LocalDate.parse("1990-01-01"));
        profile1.setAddress("北京市朝阳区");
        profile1.setUser(user1);
        user1.setUserProfile(profile1);

        // 创建用户2及其详情
        User user2 = new User();
        user2.setUsername("lisi");
        user2.setPassword("123456");
        user2.setEmail("lisi@example.com");
        user2.setPhone("13800138001");
        user2.setStatus(1);

        UserProfile profile2 = new UserProfile();
        profile2.setRealName("李四");
        profile2.setGender(0);
        profile2.setBirthday(LocalDate.parse("1992-05-15"));
        profile2.setAddress("上海市浦东新区");
        profile2.setUser(user2);
        user2.setUserProfile(profile2);

        // 创建用户3及其详情
        User user3 = new User();
        user3.setUsername("wangwu");
        user3.setPassword("123456");
        user3.setEmail("wangwu@example.com");
        user3.setPhone("13800138002");
        user3.setStatus(1);

        UserProfile profile3 = new UserProfile();
        profile3.setRealName("王五");
        profile3.setGender(1);
        profile3.setBirthday(LocalDate.parse("1988-10-20"));
        profile3.setAddress("广州市天河区");
        profile3.setUser(user3);
        user3.setUserProfile(profile3);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        System.out.println("✓ 创建了3个用户及其详情：");
        System.out.println("  - zhangsan (张三) - 北京市朝阳区");
        System.out.println("  - lisi (李四) - 上海市浦东新区");
        System.out.println("  - wangwu (王五) - 广州市天河区");


    }

    /**
     * 初始化部门和员工数据（一对多关系）
     */
    private void initDepartmentAndEmployee() {
        System.out.println("\n【一对多：部门与员工】");

        // 创建部门
        Department dept1 = new Department();
        dept1.setDeptName("研发部");
        dept1.setDescription("负责产品研发和技术创新");

        Department dept2 = new Department();
        dept2.setDeptName("销售部");
        dept2.setDescription("负责市场销售和客户关系");

        Department dept3 = new Department();
        dept3.setDeptName("人力资源部");
        dept3.setDescription("负责人员招聘和培训");

        departmentRepository.save(dept1);
        departmentRepository.save(dept2);
        departmentRepository.save(dept3);

        System.out.println("✓ 创建了3个部门：研发部、销售部、人力资源部");

        // 创建员工
        Employee emp1 = new Employee();
        emp1.setEmpName("张三");
        emp1.setPosition("高级Java工程师");
        emp1.setSalary(25000.0);
        emp1.setDepartment(dept1);

        Employee emp2 = new Employee();
        emp2.setEmpName("李四");
        emp2.setPosition("前端工程师");
        emp2.setSalary(20000.0);
        emp2.setDepartment(dept1);

        Employee emp3 = new Employee();
        emp3.setEmpName("王五");
        emp3.setPosition("测试工程师");
        emp3.setSalary(18000.0);
        emp3.setDepartment(dept1);

        Employee emp4 = new Employee();
        emp4.setEmpName("赵六");
        emp4.setPosition("销售经理");
        emp4.setSalary(22000.0);
        emp4.setDepartment(dept2);

        Employee emp5 = new Employee();
        emp5.setEmpName("钱七");
        emp5.setPosition("销售代表");
        emp5.setSalary(15000.0);
        emp5.setDepartment(dept2);

        Employee emp6 = new Employee();
        emp6.setEmpName("孙八");
        emp6.setPosition("HR专员");
        emp6.setSalary(16000.0);
        emp6.setDepartment(dept3);

        employeeRepository.save(emp1);
        employeeRepository.save(emp2);
        employeeRepository.save(emp3);
        employeeRepository.save(emp4);
        employeeRepository.save(emp5);
        employeeRepository.save(emp6);

        System.out.println("✓ 创建了6名员工");
        System.out.println("  - 研发部：张三、李四、王五");
        System.out.println("  - 销售部：赵六、钱七");
        System.out.println("  - 人力资源部：孙八");
    }
    private void initStudentAndCourse() {
        // 创建课程
        Course course1 = new Course();
        course1.setCourseName("Java程序设计");
        course1.setCredit(4);

        Course course2 = new Course();
        course2.setCourseName("Python数据分析");
        course2.setCredit(3);

        Course course3 = new Course();
        course3.setCourseName("MySQL数据库");
        course3.setCredit(2);

        CourseRepository.save(course1);
        CourseRepository.save(course2);
        CourseRepository.save(course3);

        // 创建学生并选课
        Student student1 = new Student();
        student1.setStudentName("张小明");
        student1.setStudentNo("2021001");
        student1.getCourses().add(course1);
        student1.getCourses().add(course3);

        Student student2 = new Student();
        student2.setStudentName("李小红");
        student2.setStudentNo("2021002");
        student2.getCourses().add(course1);
        student2.getCourses().add(course2);

        StudentRepository.save(student1);
        StudentRepository.save(student2);
    }
}
