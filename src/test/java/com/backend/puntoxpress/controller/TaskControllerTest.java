package com.backend.puntoxpress.controller;

//
//@WebMvcTest(TaskController.class)
//class TaskControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private TaskService taskService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private TaskDTO taskDTO;
//
//    @BeforeEach
//    void setUp() {
//        taskDTO = new TaskDTO();
//        taskDTO.setId(1L);
//        taskDTO.setTitle("Test Task");
//        taskDTO.setDescription("Test Task Description");
//        taskDTO.setCompleted(false);
//        taskDTO.setUserId(1L);
//    }
//
//    @Test
//    void testGetAllTasks() throws Exception {
//        List<TaskDTO> tasks = Collections.singletonList(taskDTO);
//        when(taskService.getAllTasks()).thenReturn(tasks);
//
//        mockMvc.perform(get("/api/tasks/all"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].title").value("Test Task"))
//                .andExpect(jsonPath("$[0].description").value("Test Task Description"));
//
//        verify(taskService, times(1)).getAllTasks();
//    }
//
//    @Test
//    void testGetUserTasks() throws Exception {
//        List<TaskDTO> tasks = Collections.singletonList(taskDTO);
//        when(taskService.getTasksByUserId(1L)).thenReturn(tasks);
//
//        mockMvc.perform(get("/api/tasks/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].title").value("Test Task"))
//                .andExpect(jsonPath("$[0].description").value("Test Task Description"));
//
//        verify(taskService, times(1)).getTasksByUserId(1L);
//    }
//
//    @Test
//    void testCreateTask() throws Exception {
//        when(taskService.createTask(any(TaskDTO.class))).thenReturn(taskDTO);
//
//        mockMvc.perform(post("/api/tasks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(taskDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.title").value("Test Task"))
//                .andExpect(jsonPath("$.description").value("Test Task Description"));
//    }
//
//    @Test
//    void testUpdateTask() throws Exception {
//        TaskDTO updatedTaskDTO = new TaskDTO();
//        updatedTaskDTO.setId(1L);
//        updatedTaskDTO.setTitle("Updated Task");
//        updatedTaskDTO.setDescription("Updated Task Description");
//        updatedTaskDTO.setCompleted(true);
//        updatedTaskDTO.setUserId(1L);
//
//        when(taskService.updateTask(eq(1L), any(TaskDTO.class))).thenReturn(updatedTaskDTO);
//
//        mockMvc.perform(put("/api/tasks/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedTaskDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value("Updated Task"))
//                .andExpect(jsonPath("$.description").value("Updated Task Description"));
//    }
//
//    @Test
//    void testDeleteTask() throws Exception {
//        doNothing().when(taskService).deleteTask(1L);
//
//        mockMvc.perform(delete("/api/tasks/1"))
//                .andExpect(status().isNoContent());
//
//        verify(taskService, times(1)).deleteTask(1L);
//    }
//
//    @Test
//    void testDeleteTaskNotFound() throws Exception {
//        doThrow(new TaskNotFoundException("Task not found with ID: 1")).when(taskService).deleteTask(1L);
//
//        mockMvc.perform(delete("/api/tasks/1"))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string("Task not found with ID: 1"));
//    }
//}
